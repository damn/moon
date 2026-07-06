(ns tx.show-modal
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.actor.set-name :as set-name]
            [com.badlogic.gdx.utils.align :as align]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.stage.add-actor :as add-actor]
            [clojure.gdx.window.set-modal :as set-modal]
            [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.ui.label :as label]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]))

(defn f
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (find-actor/f (:stage/root stage) "moon.ui.modal-window")))
  (add-actor/f stage
               (doto (window/create
                      {:title title
                       :skin skin
                       :table/rows [[{:actor (label/create
                                              {:text text
                                               :skin skin})}]
                                    [{:actor (doto (text-button/create {:text button-text :skin skin})
                                               (add-listener/f (change-listener/create
                                                                (fn [_event _actor]
                                                                  (remove/f (find-actor/f (:stage/root stage) "moon.ui.modal-window"))
                                                                  (on-click)))))}]]})
                 (set-modal/f true)
                 (set-name/f "moon.ui.modal-window")
                 (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                 (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                                align/center)))
  nil)
