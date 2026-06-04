(ns gdx.scenes.scene2d.ui.dev-menu
  (:require [clojure.gdx.scene2d.actor.create :as actor]
            [clojure.gdx.scene2d.actor.set-touchable :refer [set-touchable!]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]
            [clojure.gdx.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.ui.widget-group.set-fill-parent :refer [set-fill-parent!]]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.touchable :as touchable]
            [gdx.scenes.scene2d.ui.dev-menu.add-upd-label :refer [add-upd-label!]]))

(defn- create-window [skin label items]
  {:title label
   :skin skin
   :window/close-button? skin
   :table/rows [(for [{:keys [label on-click]} items]
                  {:actor
                   (doto (text-button/create {:text label :skin skin})
                     (add-listener! (change-listener/create
                                     (fn [event actor]
                                       (on-click actor (:stage/ctx (event/stage event)))))))})]})

(defn- main-table [skin menus update-labels]
  (let [table (table/create
               {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/create {:text label :skin skin})
                                  (add-listener! (change-listener/create
                                                  (fn [event actor]
                                                    (add-actor! (event/stage event)
                                                                (window/create (create-window skin label items)))))))})]})]
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
