(ns clojure.add-upd-label
  (:require [clojure.image :as image]
            [clojure.group :as group]
            [clojure.ui-label :as label]
            [clojure.ui-table :as table]
            [clojure.table.add-cell :refer [add-cell!]]
            [clojure.set-label-text-actor :refer [set-label-text-actor]]))

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
