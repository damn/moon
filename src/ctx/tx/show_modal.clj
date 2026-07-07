(ns ctx.tx.show-modal
  (:require [clojure.stage :as stage]
            [clojure.window :as gdx-window]
            [clojure.group :as group]
            [clojure.actor :as actor]
            [clojure.align :as align]
            [gdx.scene2d.actor.set-position :refer [set-position!]]
            [gdx.scene2d.ui.label :as label]
            [gdx.scene2d.ui.text-button :as text-button]
            [gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scene2d.ui.window :as window]))

(defn f
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (group/find-actor (:stage/root stage) "moon.ui.modal-window")))
  (stage/add-actor! stage
               (doto (window/create
                      {:title title
                       :skin skin
                       :table/rows [[{:actor (label/create
                                              {:text text
                                               :skin skin})}]
                                    [{:actor (doto (text-button/create {:text button-text :skin skin})
                                               (actor/add-listener! (change-listener/create
                                                                (fn [_event _actor]
                                                                  (actor/remove! (group/find-actor (:stage/root stage) "moon.ui.modal-window"))
                                                                  (on-click)))))}]]})
                 (gdx-window/set-modal! true)
                 (actor/set-name! "moon.ui.modal-window")
                 (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                 (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                                align/center)))
  nil)
