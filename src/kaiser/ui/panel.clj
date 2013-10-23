(ns kaiser.ui.panel "The inner content of the frame"
  (:use seesaw.core
        seesaw.dev
        :reload)
  (:import java.io.File
           javax.imageio.ImageIO))

(def ^:private card-width 62)
(def ^:private card-height 90)

(defn load-image [path]
  (ImageIO/read (File. path)))

(def ^:private card-images
  (let [cards (load-image "res/svg-cards.png")]
    (vec (for [x-tile (range 13)]
           (vec (for [y-tile (range 5)]
                  (.getSubimage cards (* x-tile card-width) (* y-tile card-height) card-width card-height)))))))

(defn- card-image [card]
  ((card-images (mod (dec (:value card)) 13))
   ((:suit card) {:clubs 0, :diamonds 1, :hearts 2, :spades 3}))) ; The order of rows in the image

(defn- sort-hand [hand]
  (sort-by #(- (* ((:suit %) {:clubs 1, :diamonds 0, :hearts 2, :spades 3})
                  14)
               (:value %))
           hand))

(defn- add-to-panel! [panel item]
  (config! panel
           :items (conj (config panel :items)
                        item)))

(defn- draw-image! [panel image location]
  (let [image-label (doto
                      (label
                        :icon image
                        :location location)
                      (config! :bounds :preferred))]
    (do (add-to-panel! panel image-label)
        image-label)))

(defn hand-display! [panel hand listener] ; listener is called with the card as a parameter
  (let [height (.getHeight panel)
        y (- height 100)
        width (.getWidth panel)
        start-x (/ (- width
                      (* card-width (count hand)))
                   2)
        sorted-hand (sort-hand hand)]
    (doseq [i (range (count sorted-hand))]
      (let [card (nth sorted-hand i)]
        (do (draw-image! panel (card-image card) [(+ start-x (* i card-width)), y])
            (listen :mouse-clicked (fn [e] (listener card))))))))

(defn turn-display! [panel turn]
  (let [{cards :cards leader :leader} turn
        height (.getHeight panel)
        width (.getWidth panel)
        x-padding 20
        y-padding 20
        positions [[(/ (- width card-width) 2)
                    (+ y-padding (/ height 2))]
                   [(+ x-padding (/ width 2))
                    (/ (- height card-height) 2)]
                   [(/ (- width card-width) 2)
                    (- (/ height 2) y-padding)]
                   [(- (/ width 2) x-padding)
                    (/ (- height card-height) 2)]]]
    (doseq [i (range (count cards))]
      (let [index (mod (+ i leader) 4)]
        (draw-image! panel (card-image (cards index)) (positions index))))))

(def panel (xyz-panel :background "#060"))

#_(def ^:private content
  (atom
    {:hand (hand-display! panel (:hand @impl/round))
     :played-cards (played-cards-display! panel (:turn @impl/round))
     :score (score-display! panel (:score @impl/game))}))
(show-events (label))
