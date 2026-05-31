(ns entity.state.enter.player-dead
  (:require [game.state :as state]))

(defmethod state/enter :player-dead
  [_ _eid]
  [[:tx/sound "bfxr_playerdeath"]
   [:tx/show-modal {:title "YOU DIED - again!"
                    :text "Good luck next time!"
                    :button-text "OK"
                    :on-click (fn [])}]])
