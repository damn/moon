(ns clojure.main-table
  (:require
            [clojure.scene2d.actor.add-listener] [clojure.stage :as stage]
            [clojure.event :as event]
            [clojure.utils-change-listener :as change-listener]
            [clojure.ui-text-button :as text-button]
            [clojure.window.add-close-button :as add-close-button]
            [clojure.add-upd-label :refer [add-upd-label!]]
            [clojure.ui-table :as table]
            [clojure.ui-window :as window]
            [clojure.set-ctx :as set-ctx]))

(defn f [skin menus update-labels]
  (let [table (table/create
               {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/create {:text label :skin skin})
                                  (clojure.scene2d.actor.add-listener/f (change-listener/create
                                                   (fn [event actor]
                                                     (stage/add-actor! (event/get-stage event)
                                                                  (doto (window/create
                                                                         {:title label
                                                                          :skin skin
                                                                          :table/rows [(for [{:keys [label on-click]} items]
                                                                                         {:actor
                                                                                          (doto (text-button/create {:text label :skin skin})
                                                                                            (clojure.scene2d.actor.add-listener/f (change-listener/create
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
