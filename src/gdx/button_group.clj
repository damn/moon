(ns gdx.button-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup)))

(defn create
  [{:keys [max-check-count
           min-check-count]}]
  (doto (ButtonGroup.)
    (.setMaxCheckCount (int max-check-count))
    (.setMinCheckCount (int min-check-count))))

(defn add! [button-group button]
  (.add ^ButtonGroup button-group ^Button button))

(defn remove! [button-group button]
  (.remove ^ButtonGroup button-group ^Button button))

(defn get-checked [button-group]
  (.getChecked ^ButtonGroup button-group))
