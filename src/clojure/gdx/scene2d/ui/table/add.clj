(ns clojure.gdx.scene2d.ui.table.add
  (:require [clojure.gdx.scene2d.ui.cell :as cell])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add! [table cell-declaration]
  (-> (Table/.add table ^Actor (:actor cell-declaration))
      (cell/set-opts! (dissoc cell-declaration :actor))))
