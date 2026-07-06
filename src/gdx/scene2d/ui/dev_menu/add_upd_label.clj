(ns gdx.scene2d.ui.dev-menu.add-upd-label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [gdx.scene2d.ui.label :as label]
            [gdx.scene2d.ui.table :as table]
            [gdx.scene2d.ui.table.add-cell :refer [add-cell!]]
            [gdx.scene2d.ui.dev-menu.set-label-text-actor :refer [set-label-text-actor]]))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create
                {:text ""
                 :skin skin})
         sub-table (table/create
                    {:table/rows [[{:actor (image/new-texture icon)}
                                   label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (add-cell! table {:actor sub-table
                       :right? true
                       :expand-x? true})))
  ([skin table text-fn]
   (let [label (label/create
                {:text ""
                 :skin skin})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (add-cell! table {:actor label
                       :right? true
                       :expand-x? true}))))
