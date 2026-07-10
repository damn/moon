(ns clojure.ui.dev-menu.main-table
  (:require
            
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.ui.dev-menu.main-table.add-upd-label :refer [add-upd-label!]]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [clojure.set-ctx :as set-ctx]))

(defn f [skin menus update-labels]
  (let [table (doto (table/new)
    (table-set-opts/set-opts! {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/new label skin)
                                  (actor/addListener (change-listener/create
                                                   (fn [event actor]
                                                     (stage/addActor (event/getStage event)
                                                                  (doto (doto (window/new label skin)
    (table-set-opts/set-opts! {:title label
                                                                          :skin skin
                                                                          :table/rows [(for [{:keys [label on-click]} items]
                                                                                         {:actor
                                                                                          (doto (text-button/new label skin)
                                                                                            (actor/addListener (change-listener/create
                                                                                                             (fn [event actor]
                                                                                                               (let [stage (event/getStage event)]
                                                                                                                 (set-ctx/f stage
                                                                                                                            (on-click (:stage/ctx stage))))))))})]}))
                                                                    (add-close-button/f! skin)))))))})]}))]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))
