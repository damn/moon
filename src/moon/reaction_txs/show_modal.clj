(ns moon.reaction-txs.show-modal
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [moon.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Align)))

(defn do!
  [{:keys [^Skin ctx/skin
           ^Stage ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (-> stage
                   .getRoot
                   (.findActor "moon.ui.modal-window"))))
  (stage/add-actor! stage
                    (doto (Window. ^String title skin)
                      (.add (label/create text skin))
                      (.row)
                      (.add (doto (TextButton. ^String button-text skin)
                              (.addListener
                               (proxy [ChangeListener] []
                                 (changed [_event _actor]
                                   (.remove (-> stage .getRoot (.findActor "moon.ui.modal-window")))
                                   (on-click))))))
                      (.setModal true)
                      (.pack)
                      (.setName "moon.ui.modal-window")
                      (.setPosition (/ (.getWorldWidth  (.getViewport stage)) 2)
                                    (* (.getWorldHeight (.getViewport stage)) (/ 3 4))
                                    Align/center)))
  ctx)


; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (do! @moon.application/state
                       {:title "TestTitle"
                        :text "TextTEXT"
                        :button-text "testbuttonTEXT"
                        :on-click (fn [])})))

 )
