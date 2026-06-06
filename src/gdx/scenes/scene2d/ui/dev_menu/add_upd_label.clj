(ns gdx.scenes.scene2d.ui.dev-menu.add-upd-label
  (:require [gdx.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scene2d.group.add-actor :refer [add-actor!]]
            [gdx.scene2d.ui.table.add-cell :refer [add-cell!]]
            [gdx.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor :refer [set-label-text-actor]]))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create
                {:text ""
                 :skin skin})
         sub-table (table/create
                    {:table/rows [[{:actor (image/create icon)}
                                   label]]})]
     (add-actor! table (set-label-text-actor label text-fn))
     (add-cell! table {:actor sub-table
                       :right? true
                       :expand-x? true})))
  ([skin table text-fn]
   (let [label (label/create
                {:text ""
                 :skin skin})]
     (add-actor! table (set-label-text-actor label text-fn))
     (add-cell! table {:actor label
                       :right? true
                       :expand-x? true}))))
