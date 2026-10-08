(ns editor.ui.widget.enum
  (:require [clojure.edn :as edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn value [widget]
  (edn/read-string (.getSelected ^SelectBox widget)))

(defn create [state schema v]
  (doto ^SelectBox (SelectBox. (:skin @state))
    (.setItems ^"[Ljava.lang.Object;" (into-array (map pr-str (rest schema))))
    (.setSelected (pr-str v))))
