(ns moon.reaction-txs.show-modal
  (:require [moon.ui.text-button :as text-button]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (-> stage
                   .getRoot
                   (.findActor "moon.ui.modal-window"))))
  (.addActor stage
             (window/create
              {:title title
               :rows [[{:actor (Label. text ^Skin skin)}]
                      [{:actor (text-button/create
                                {:text button-text
                                 :on-clicked (fn [_actor _ctx]
                                               (.remove
                                                (-> stage
                                                    .getRoot
                                                    (.findActor "moon.ui.modal-window")))
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
