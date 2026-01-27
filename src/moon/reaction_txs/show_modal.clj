(ns moon.reaction-txs.show-modal
  (:require [moon.ui.actor :as actor]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (-> stage
                   Stage/.getRoot
                   (.findActor "moon.ui.modal-window"))))
  (Stage/.addActor stage
                   (doto (window/create
                          {:title title
                           :rows [[{:actor (Label. ^String text ^Skin skin)}]
                                  [{:actor (text-button/create
                                            {:text button-text
                                             :on-clicked (fn [_actor _ctx]
                                                           (Actor/.remove
                                                            (-> stage
                                                                Stage/.getRoot
                                                                (.findActor "moon.ui.modal-window")))
                                                           (on-click))
                                             :skin skin})}]]
                           :modal? true
                           :skin skin})
                     (.pack)
                     (.setName "moon.ui.modal-window")
                     (actor/set-center! [(/ (Viewport/.getWorldWidth  (Stage/.getViewport stage)) 2)
                                         (* (Viewport/.getWorldHeight (Stage/.getViewport stage)) (/ 3 4))])
                     ))
  ctx)


; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.
