(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.button-group
  "Manages a group of buttons to enforce a minimum and maximum number of checked buttons. This enables \"radio button\"
  functionality and more. A button may only be in one group at a time.
  
  The can check method can be overridden to control if a button check or uncheck is allowed."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup)))

(defn create []
  (ButtonGroup.))

(defn add!
  "Adds a new cell with a label. This may only be called if a skin has been set with table or
  set skin."
  [^ButtonGroup button-group ^Button button]
  (.add button-group button))

(defn remove!
  "Removes this actor from its parent, if it has a parent."
  [^ButtonGroup button-group ^Button button]
  (.remove button-group button))

(defn checked [^ButtonGroup button-group]
  (.getChecked button-group))

(defn set-max-check-count!
  "Sets the maximum number of buttons that can be checked. Set to -1 for no maximum. Default is 1."
  [^ButtonGroup button-group n]
  (.setMaxCheckCount button-group (int n)))

(defn set-min-check-count!
  "Sets the minimum number of buttons that must be checked. Default is 1."
  [^ButtonGroup button-group n]
  (.setMinCheckCount button-group (int n)))
