(ns scene2d.ui.table.add-cell
  (:require [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add-cell! [^Table table cell-declaration]
  (-> (Table/.add table ^Actor (:actor cell-declaration))
      (gdx/set-opts! (dissoc cell-declaration :actor))))
