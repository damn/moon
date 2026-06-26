(ns tx.show-modal
  (:require [com.badlogic.gdx.utils.align :as align]
            [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.actor.remove :refer [remove!]]
            [scene2d.actor.set-name :refer [set-name!]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.ui.label :as label]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.window.set-modal :as set-modal]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.group.find-actor :refer [find-actor]]
            [scene2d.stage.add-actor :refer [add-actor!]]))

(defn f
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (find-actor (:stage/root stage) "moon.ui.modal-window")))
  (add-actor! stage
              (doto (window/create
                     {:title title
                      :skin skin
                      :table/rows [[{:actor (label/create
                                             {:text text
                                              :skin skin})}]
                                   [{:actor (doto (text-button/create {:text button-text :skin skin})
                                              (add-listener! (change-listener/create
                                                              (fn [_event _actor]
                                                                (remove! (find-actor (:stage/root stage) "moon.ui.modal-window"))
                                                                (on-click)))))}]]})
                (set-modal/f! true)
                (set-name! "moon.ui.modal-window")
                (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                               align/center)))
  nil)
