(ns com.badlogic.gdx.scenes.scene2d.ui.button-group
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup)))

(defn new []
  (ButtonGroup.))

(defn add [^ButtonGroup button-group ^Button button]
  (.add button-group button))

(defn getChecked [^ButtonGroup button-group]
  (.getChecked button-group))

(defn remove [^ButtonGroup button-group ^Button button]
  (.remove button-group button))

(defn setMaxCheckCount [^ButtonGroup button-group n]
  (.setMaxCheckCount button-group (int n)))

(defn setMinCheckCount [^ButtonGroup button-group n]
  (.setMinCheckCount button-group (int n)))
