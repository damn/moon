(ns clojure.editor.widget.one-to-one
  (:require [gdl.get-user-object :refer [get-user-object]]
            [clojure.editor.widget.one-to-one.add-one-to-one-rows :refer [add-one-to-one-rows]]
            [gdl.children :refer [children]]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn create [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defn value [_  widget _schemas]
  (->> (children widget)
       (keep get-user-object)
       first))
