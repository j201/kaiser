(ns kaiser.ui.frame
  (:use [kaiser.game.implementation :as impl]
        ;[kaiser.ui.canvas :as cvs]
        [kaiser.ui.panel :as pnl]
        seesaw.core
       :reload))

(native!)

(def ^:private my-frame (atom nil))

(declare show-stats!)
(declare show-preferences!)
(declare save-game!)
(declare load-game!)
(defn exit! [& _] (do (print "closing") (.dispose @my-frame)))
(declare show-manual!)
(declare show-about!)

(def ^:private game-menu
  (menu :text "Game" :items
        [(action
           :name "New Game"
           :key "ctrl N"
           :tip "Starts a new game."
           :handler (fn [] nil));#(do (impl/new-game!) (cvs/canvas-repaint!)))
         (action
           :name "Save Game"
           :key "ctrl S"
           :tip "Saves the current game."
           :handler save-game!)
         (action
           :name "Load Game"
           :key "ctrl L"
           :tip "Loads a saved game."
           :handler load-game!)
         (action
           :name "Statistics"
           :key "alt S"
           :tip "Shows past playing statistics."
           :handler show-stats!)
         (action
           :name "Preferences"
           :key "ctrl P"
           :tip "Change game settings"
           :handler show-preferences!)
         (action
           :name "Exit"
           :key "alt F4"
           :tip "Exits the program."
           :handler exit!)]))

(def ^:private help-menu
  (menu :text "Help" :items
        [(action
           :name "Manual"
           :key "F1"
           :tip "Manual and game rules"
           :handler show-manual!)
         (action
           :name "About"
           :handler show-about!)]))

(defn make-frame []
  (do
    (when @my-frame
      (.dispose @my-frame))
    (reset! my-frame 
            (doto
              (frame
                :title "Kaiser"
                :size [800 :by 600]
                :menubar (menubar :items [game-menu help-menu])
                ;:on-close :nothing
                :content pnl/panel)
              (listen :window-closing exit!)
              show!))))

(make-frame)
(impl/new-game!)
(impl/new-round!)
(pnl/hand-display! pnl/panel (first (:hands @impl/round)) prn)
(repaint! pnl/panel)

(print (config pnl/panel :items))
