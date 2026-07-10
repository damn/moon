(ns clojure.ui.dev-menu.main-table.add-upd-label
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.set-label-text-actor :refer [set-label-text-actor]]))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/new "" skin)
         sub-table (doto (table/new)
    (table-set-opts/set-opts! {:table/rows [[{:actor (image/newTexture icon)}
                                   label]]}))]
     (group/addActor table (set-label-text-actor label text-fn))
     (add-cell! table {:actor sub-table
                       :right? true
                       :expand-x? true})))
  ([skin table text-fn]
   (let [label (label/new "" skin)]
     (group/addActor table (set-label-text-actor label text-fn))
     (add-cell! table {:actor label
                       :right? true
                       :expand-x? true}))))
