(ns cdq.ui.menu
  (:require [cdq.ui.table :as table]
            [cdq.ui.stage :as stage]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.group :as group]
            [clojure.gdx.scene2d.ui.cell :as cell]
            [clojure.gdx.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [cdq.ui.image :as image]
            [clojure.vis-ui.menu :as menu]
            [clojure.vis-ui.menu-bar :as menu-bar]
            [clojure.vis-ui.menu-item :as menu-item]
            [clojure.vis-ui.popup-menu :as popup-menu]
            [clojure.vis-ui.label :as vis-label]))

(defn- set-label-text-actor [label text-fn]
  (actor/create
   {:act (fn [this delta]
           (when-let [stage (actor/stage this)]
             (label/set-text! label (text-fn (stage/ctx stage)))))
    :draw (fn [this batch parent-alpha])}))

(defn add-upd-label!
  ([table text-fn icon]
   (let [label (vis-label/create "")
         sub-table (table/create
                    {:rows [[{:actor (image/create {:image/object icon})}
                             label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (table/add! table sub-table)))))
  ([table text-fn]
   (let [label (vis-label/create "")]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (table/add! table label))))))

(defn- add-update-labels! [menu-bar update-labels]
  (let [table (menu-bar/table menu-bar)]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! table update-fn icon)
          (add-upd-label! table update-fn))))))

(defn- add-menu! [menu-bar {:keys [label items]}]
  (let [app-menu (menu/create label)]
    (doseq [{:keys [label on-click]} items]
      (popup-menu/add-item! app-menu (doto (menu-item/create label)
                                       (actor/add-listener!
                                        (change-listener/create
                                          (fn [event actor]
                                            (when on-click
                                              (on-click actor (stage/ctx (event/stage event))))))))))
    (menu-bar/add-menu! menu-bar app-menu)))

(defn create [{:keys [menus update-labels]}]
  (let [menu-bar (menu-bar/create)]
    (run! #(add-menu! menu-bar %) menus)
    (add-update-labels! menu-bar update-labels)
    (menu-bar/table menu-bar)))
