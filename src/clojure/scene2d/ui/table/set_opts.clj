(ns clojure.scene2d.ui.table.set-opts
  (:require [clojure.set-opts :as clj]
            [clojure.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.scene2d.ui.widget-group.pack :refer [pack!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn set-opts! [table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (clj/set-opts! (Table/.defaults table) defaults)))
