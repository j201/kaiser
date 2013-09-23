(ns kaiser.ui.canvas
  (:use seesaw.core
        seesaw.color
        seesaw.dev
        [seesaw.graphics :as graphics]
        [kaiser.game.implementation :as impl]
        :reload)
  (:import [javax.imageio ImageIO]
           [java.awt.image BufferedImage]
           [java.io File]
           [java.awt.geom AffineTransform]))

(defn- load-image [path]
  (ImageIO/read (File. path)))

(defn- draw-image [g2d image x y]
  (.drawImage g2d image (AffineTransform. 1. 0. 0. 1. (double x) (double y)) nil))

(defn- fill-image [canvas g2d image]
  (let [image-width (.getWidth image)
        image-height (.getHeight image)
        x-tiles (Math/ceil (/ (.getWidth canvas) image-width))
        y-tiles (Math/ceil (/ (.getHeight canvas) image-height))]
    (doseq [x-index (range x-tiles)
            y-index (range y-tiles)]
      (draw-image g2d image (* x-index image-width) (* y-index image-height)))))

(def ^:private test-image (load-image "res/test.png"))

(def ^:private background (load-image "res/greenbackground.png"))

(def ^:private card-width 62)
(def ^:private card-height 90)

(def ^:private card-images
  (let [cards (load-image "res/svg-cards.png")]
    (vec (for [x-tile (range 13)]
           (vec (for [y-tile (range 5)]
                  (.getSubimage cards (* x-tile card-width) (* y-tile card-height) card-width card-height)))))))

(defn- card-image [card]
  ((card-images (mod (dec (:value card)) 13))
   ((:suit card) {:clubs 0, :diamonds 1, :hearts 2, :spades 3})))

(def my-canvas (canvas))

(defn- sort-hand [round]
  (sort-by #(- (* ((:suit %) {:clubs 1, :diamonds 0, :hearts 2, :spades 3})
                  14)
               (:value %))
           (first (:hands round))))

(def ^:private card-overlay-style (graphics/style :background (color "#0000ff" 32)))
(defn- draw-card-overlay [g2d x y]
  (graphics/draw g2d
                 (graphics/rect x y card-width card-height)
                 card-overlay-style))

(defn- draw-hand
  "Sorts the player's cards and displays them horizontally centered at a given location"
  [g2d round x y]
  (let [hand (sort-hand round)
        width (* card-width (count hand))
        start-x (- x (/ width 2))]
    (doseq [i (range (count hand))]
      (let [image (card-image (nth hand i))
            image-x (+ start-x (* i card-width))]
        (do
          (listen (make-widget image)
                  :mouse-entered #(draw-card-overlay g2d image-x y)) 
          (draw-image g2d (card-image (nth hand i)) (+ x (* i card-width)) y))))))

(defn- paint [canvas g2d]
  (do
    (fill-image canvas g2d background)
    (draw-hand g2d @impl/round 100 100)))

(defn canvas-repaint! []
  (config! my-canvas :paint paint))

