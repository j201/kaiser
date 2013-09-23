(ns kaiser.game.implementation
  (:use [kaiser.game.interface :as gi] :reload))

(def game (atom (gi/new-game)))
(def round (atom (gi/new-round game)))

(defn change-rules! [rules]
  (reset! game (assoc @game :rules rules)))

(defn play-card! [card]
  (reset! round (gi/play-card card round)))

(defn new-round! []
  (reset! round (gi/new-round @game)))

(defn new-game! "Starts a new game with the same rules as before" []
  (do
    (reset! game (gi/new-game (:rules @game)))
    (new-round!)))
