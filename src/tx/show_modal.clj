(ns tx.show-modal
  (:require [clojure.gdx :as gdx]
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
  (assert (not (gdx/find-actor (:stage/root stage) "moon.ui.modal-window")))
  (gdx/add-actor! stage
                  (doto (window/create
                         {:title title
                          :skin skin
                          :table/rows [[{:actor (label/create
                                                 {:text text
                                                  :skin skin})}]
                                       [{:actor (doto (text-button/create {:text button-text :skin skin})
                                                  (gdx/add-listener! (change-listener/create
                                                                      (fn [_event _actor]
                                                                        (gdx/remove! (gdx/find-actor (:stage/root stage) "moon.ui.modal-window"))
                                                                        (on-click)))))}]]})
                    (gdx/window-set-modal! true)
                    (gdx/set-name! "moon.ui.modal-window")
                    (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                    (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                                   gdx/align-center)))
  nil)
