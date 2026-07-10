(ns clojure.ui.dev-menu.main-table.add-upd-label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [clojure.scene2d.group :as group]
            [clojure.ui-label :as label]
            [clojure.ui-table :as table]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.set-label-text-actor :refer [set-label-text-actor]]))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create
                {:text ""
                 :skin skin})
         sub-table (table/create
                    {:table/rows [[{:actor (image/newTexture icon)}
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
