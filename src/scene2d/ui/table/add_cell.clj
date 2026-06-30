(ns scene2d.ui.table.add-cell
  (:require [clojure.gdx :as gdx]))

(defn add-cell! [table cell-declaration]
  (-> (gdx/table-add! table (:actor cell-declaration))
      (gdx/set-opts! (dissoc cell-declaration :actor))))
