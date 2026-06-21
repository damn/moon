(ns clojure.scenes.scene2d.ui.table.add-cell
  (:require [clojure.scenes.scene2d.ui.cell :refer [set-opts!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add-cell! [table cell-declaration]
  (-> (Table/.add table ^Actor (:actor cell-declaration))
      (set-opts! (dissoc cell-declaration :actor))))
