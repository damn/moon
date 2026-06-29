(ns tx.show-modal
  (:require [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.ui.label :as label]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window])
  (:import (com.badlogic.gdx.utils Align)
           (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d Group)
           (scene2d Stage)))

(defn f
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (Group/.findActor (:stage/root stage) "moon.ui.modal-window")))
  (Stage/.addActor stage
                   (doto (window/create
                          {:title title
                           :skin skin
                           :table/rows [[{:actor (label/create
                                                  {:text text
                                                   :skin skin})}]
                                        [{:actor (doto (text-button/create {:text button-text :skin skin})
                                                   (Actor/.addListener (change-listener/create
                                                                        (fn [_event _actor]
                                                                          (Actor/.remove (Group/.findActor (:stage/root stage) "moon.ui.modal-window"))
                                                                          (on-click)))))}]]})
                     (.setModal true)
                     (Actor/.setName "moon.ui.modal-window")
                     (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                     (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                                    Align/center)))
  nil)
