(ns scene2d.ui.table.set-opts
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [^Table table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (.pack table))
  (when-let [defaults (:table/cell-defaults opts)]
    (gdx/set-opts! (.defaults table) defaults)))
