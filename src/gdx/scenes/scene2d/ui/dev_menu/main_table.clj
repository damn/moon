(ns gdx.scenes.scene2d.ui.dev-menu.main-table
  (:require [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.change-listener :as change-listener]
            [scene2d.event.get-stage :as get-stage]
            [scene2d.stage.add-actor :refer [add-actor!]]
            [ui.text-button :as text-button]
            [ui.window.add-close-button :as add-close-button]
            [gdx.scenes.scene2d.ui.dev-menu.add-upd-label :refer [add-upd-label!]]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]))

(defn f [skin menus update-labels]
  (let [table (table/create
               {:table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (doto (text-button/create {:text label :skin skin})
                                  (add-listener! (change-listener/create
                                                  (fn [event actor]
                                                    (add-actor! (get-stage/f event)
                                                                (doto (window/create
                                                                       {:title label
                                                                        :skin skin
                                                                        :table/rows [(for [{:keys [label on-click]} items]
                                                                                       {:actor
                                                                                        (doto (text-button/create {:text label :skin skin})
                                                                                          (add-listener! (change-listener/create
                                                                                                          (fn [event actor]
                                                                                                            (on-click actor (:stage/ctx (get-stage/f event)))))))})]})
                                                                  (add-close-button/f! skin)))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))
