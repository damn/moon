(ns clojure.ui.table.add-cell
  (:require [gdl.scenes.scene2d.ui.table :as table]
            [clojure.ui.cell.set-opts :as set-opts]))

(defn add-cell! [table cell-declaration]
  (-> (table/add! table (:actor cell-declaration))
      (set-opts/f (dissoc cell-declaration :actor))))
