(ns moon.entity.state.player-dead)

(defn enter
  [_ _eid]
  [[:tx/sound "bfxr_playerdeath"]
   [:tx/show-modal {:title "YOU DIED - again!"
                    :text "Good luck next time!"
                    :button-text "OK"
                    :on-click (fn [])}]])

(defn cursor
  [_ _eid _ctx]
  :cursors/black-x)

(defn pause-game?
  [_]
  true)
