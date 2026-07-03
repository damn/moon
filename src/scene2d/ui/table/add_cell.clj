(ns scene2d.ui.table.add-cell
  (:require [clojure.gdx.table.add :as add]
            [scene2d.ui.cell :refer [set-opts!]]))

(defn add-cell! [table cell-declaration]
  (-> (add/f table (:actor cell-declaration))
      (set-opts! (dissoc cell-declaration :actor))))
