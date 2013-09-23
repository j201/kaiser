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

(defn hand-display! [panel hand]
  (let [height (.getHeight panel)
        y (- height 100)
        width (.getWidth panel)
        start-x (/ (- width
                      (* card-width (count hand)))
                   2)
        sorted-hand (sort-hand hand)]
    (doseq [i (range (count sorted-hand))]
      (add-to-panel! panel (doto 
                             (label
                               :icon (card-image (nth sorted-hand i))
                               :location [(+ start-x (* i card-width)), y]
                               :class :card)
                             (config! :bounds :preferred)
                             (listen :mouse-clicked (fn [e] (println e))
                                     )))))) ; NECESSARY FOR DISPLAYING THE LABEL

(def panel (xyz-panel :background "#060"))

#_(def ^:private content
  (atom
    {:hand (hand-display! panel (:hand @impl/round))
     :played-cards (played-cards-display! panel (:turn @impl/round))
     :score (score-display! panel (:score @impl/game))}))
(show-events (label))
