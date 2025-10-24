(ns clojure.gdx.scene2d.ui.button-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup)))

(defn create [{:keys [min-check-count
                      max-check-count]}]
  (doto (ButtonGroup.)
    (.setMaxCheckCount max-check-count)
    (.setMinCheckCount min-check-count)))

(def checked ButtonGroup/.getChecked)

(defn add! [button-group button]
  (ButtonGroup/.add button-group ^Button button))

(defn remove! [button-group button]
  (ButtonGroup/.remove button-group ^Button button))
