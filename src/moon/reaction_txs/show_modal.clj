(ns moon.reaction-txs.show-modal
  (:require [moon.ui :as ui]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (-> stage
                   Stage/.getRoot
                   (group/find-actor "moon.ui.modal-window"))))
  (Stage/.addActor stage
                   (ui/actor
                    {:type :ui/window
                     :title title
                     :rows [[{:actor (ui/actor
                                      {:type :ui/label
                                       :label/text text
                                       :label/skin skin})}]
                            [{:actor (ui/actor
                                      {:type :ui/text-button
                                       :text button-text
                                       :on-clicked (fn [_actor _ctx]
                                                     (Actor/.remove
                                                      (-> stage
                                                          Stage/.getRoot
                                                          (group/find-actor "moon.ui.modal-window")))
                                                     (on-click))
                                       :skin skin})}]]
                     :actor/name "moon.ui.modal-window"
                     :modal? true
                     :skin skin
                     :actor/center-position [(/ (Viewport/.getWorldWidth  (Stage/.getViewport stage)) 2)
                                             (* (Viewport/.getWorldHeight (Stage/.getViewport stage)) (/ 3 4))]
                     :pack? true}))
  ctx)


; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.
