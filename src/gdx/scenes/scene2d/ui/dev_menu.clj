(ns gdx.scenes.scene2d.ui.dev-menu
  (:require [clojure.actor :as actor]
            [clojure.actor.set-touchable :refer [set-touchable!]]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.event.get-stage :refer [get-stage]]
            [clojure.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.stage.add-actor :refer [add-actor!]]
            [clojure.ui.label :as label]
            [clojure.window.add-close-button :as add-close-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.layout.set-fill-parent :refer [set-fill-parent!]]
            [clojure.change-listener :as change-listener]
            [clojure.touchable :as touchable]
            [gdx.scenes.scene2d.ui.dev-menu.add-upd-label :refer [add-upd-label!]]))

(defn- create-window [skin label items]
  {:title label
   :skin skin
   :table/rows [(for [{:keys [label on-click]} items]
                  {:actor
                   (doto (text-button/create {:text label :skin skin})
                     (add-listener! (change-listener/create
                                     (fn [event actor]
                                       (on-click actor (:stage/ctx (get-stage event)))))))})]})

(defn- main-table [skin menus update-labels]
  (let [table (table/create
               {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/create {:text label :skin skin})
                                  (add-listener! (change-listener/create
                                                  (fn [event actor]
                                                    (add-actor! (get-stage event)
                                                                (doto (window/create (create-window skin label items))
                                                                  (add-close-button/f! skin)))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  [{:keys [menus update-labels skin]}]
  (doto (table/create
         {:table/rows [[{:actor (main-table skin menus update-labels)
                         :expand-x? true
                         :fill-x? true
                         :colspan 1}]
                       [{:actor (doto (label/create
                                       {:text ""
                                        :skin skin})
                                  (set-touchable! touchable/disabled))
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]})
    (set-fill-parent! true)))
