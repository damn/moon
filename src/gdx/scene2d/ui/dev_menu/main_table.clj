(ns gdx.scene2d.ui.dev-menu.main-table
  (:require [clojure.stage :as stage]
            [clojure.actor :as actor]
            [clojure.event :as event]
            [gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scene2d.ui.text-button :as text-button]
            [gdx.scene2d.ui.window.add-close-button :as add-close-button]
            [gdx.scene2d.ui.dev-menu.add-upd-label :refer [add-upd-label!]]
            [gdx.scene2d.ui.table :as table]
            [gdx.scene2d.ui.window :as window]
            [gdx.scene2d.stage :refer [set-ctx!]]))

(defn f [skin menus update-labels]
  (let [table (table/create
               {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/create {:text label :skin skin})
                                  (actor/add-listener! (change-listener/create
                                                   (fn [event actor]
                                                     (stage/add-actor! (event/get-stage event)
                                                                  (doto (window/create
                                                                         {:title label
                                                                          :skin skin
                                                                          :table/rows [(for [{:keys [label on-click]} items]
                                                                                         {:actor
                                                                                          (doto (text-button/create {:text label :skin skin})
                                                                                            (actor/add-listener! (change-listener/create
                                                                                                             (fn [event actor]
                                                                                                               (let [stage (event/get-stage event)]
                                                                                                                 (set-ctx! stage
                                                                                                                            (on-click (:stage/ctx stage))))))))})]})
                                                                    (add-close-button/f! skin)))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))
