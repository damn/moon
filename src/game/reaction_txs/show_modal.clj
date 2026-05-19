(ns game.reaction-txs.show-modal
  (:require [gdl.scene2d.stage :as stage]
            [gdl.scene2d.actor :as actor]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (stage/find-actor stage "moon.ui.modal-window")))
  (stage/add-actor! stage
                    {:type :ui/window
                     :title title
                     :skin skin
                     :window/modal? true
                     :table/rows [[{:actor (actor/create
                                            {:type :ui/label
                                             :text text
                                             :skin skin})}]
                                  [{:actor (actor/create
                                            {:type :ui/text-button
                                             :text button-text
                                             :skin skin
                                             :actor/listeners {:listener/change (fn [_event _actor]
                                                                                  (actor/remove! (stage/find-actor stage "moon.ui.modal-window"))
                                                                                  (on-click))}})}]]
                     :actor/name "moon.ui.modal-window"
                     :actor/position [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                      (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))
                                      :align/center]})
  ctx)

; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (:tx/show-modal @dev.application/state
                                  {:title "TestTitle"
                                   :text "TextTEXT"
                                   :button-text "testbuttonTEXT"
                                   :on-click (fn [])})))

 )
