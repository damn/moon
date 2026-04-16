(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.button-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup)))

(defn create [{:keys [max-check-count min-check-count]}]
  (doto (ButtonGroup.)
    (.setMaxCheckCount max-check-count)
    (.setMinCheckCount min-check-count)))

(defn add! [^ButtonGroup button-group ^Button button]
  (.add button-group button))

(defn remove! [^ButtonGroup button-group ^Button button]
  (.remove button-group button))

(defn checked [^ButtonGroup button-group]
  (.getChecked button-group))
