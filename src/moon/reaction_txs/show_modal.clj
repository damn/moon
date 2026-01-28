(ns moon.reaction-txs.show-modal
  (:require [moon.ui.actor :as actor]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
                                               Window)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [^Skin ctx/skin
           ^Stage ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (-> stage
                   .getRoot
                   (.findActor "moon.ui.modal-window"))))
  (.addActor stage
             (doto (Window. ^String title skin)
               (table/add-rows! [[{:actor (Label. ^String text skin)}]
                                 [{:actor (text-button/create
                                           {:text button-text
                                            :on-clicked (fn [_actor _ctx]
                                                          (.remove (-> stage .getRoot (.findActor "moon.ui.modal-window")))
                                                          (on-click))
                                            :skin skin})}]])
               (.setModal true)
               (.pack)
               (.setName "moon.ui.modal-window")
               (actor/set-center! [(/ (.getWorldWidth  (.getViewport stage)) 2)
                                   (* (.getWorldHeight (.getViewport stage)) (/ 3 4))])))
  ctx)


; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.
