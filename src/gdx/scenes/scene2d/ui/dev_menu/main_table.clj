(ns gdx.scenes.scene2d.ui.dev-menu.main-table
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.stage.add-actor :as add-actor]
            [clojure.gdx.stage.set-ctx :as set-ctx]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [gdx.scenes.scene2d.ui.dev-menu.add-upd-label :refer [add-upd-label!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]))

(defn f [skin menus update-labels]
  (let [table (table/create
               {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/create {:text label :skin skin})
                                  (add-listener/f (change-listener/create
                                                   (fn [event actor]
                                                     (add-actor/f (event/get-stage event)
                                                                  (doto (window/create
                                                                         {:title label
                                                                          :skin skin
                                                                          :table/rows [(for [{:keys [label on-click]} items]
                                                                                         {:actor
                                                                                          (doto (text-button/create {:text label :skin skin})
                                                                                            (add-listener/f (change-listener/create
                                                                                                             (fn [event actor]
                                                                                                               (let [stage (event/get-stage event)]
                                                                                                                 (set-ctx/f stage
                                                                                                                            (on-click (:stage/ctx stage))))))))})]})
                                                                    (add-close-button/f! skin)))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))
