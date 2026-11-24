(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.horizontal-group
  "A group that lays out its children side by side horizontally, with optional wrapping. This can be easier than using
  [[clojure.com.badlogic.gdx.scenes.scene2d.ui.table]] when actors need to be inserted into or removed from the middle of the group. [clojure.com.badlogic.gdx.scenes.scene2d.group/children] can be
  sorted to change the order of the actors (eg set zindex). invalidate must be called after changing
  the children order.
  
  The preferred width is the sum of the children's preferred widths plus spacing. The preferred height is the largest preferred
  height of any child. The preferred size is slightly different when wrap is enabled. The min size is the
  preferred size and the max size is 0.
  
  Widgets are sized using their get pref width, so widgets which return 0 as their preferred width
  will be given a width of 0 (eg, a label with set wrap enabled)."
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create []
  (HorizontalGroup.))

(defn space!
  "Sets the horizontal space between children."
  [^HorizontalGroup horizontal-group space]
  (.space horizontal-group (float space)))

(defn pad!
  "Sets the padTop, padLeft, padBottom, and padRight to the specified value."
  [^HorizontalGroup horizontal-group pad]
  (.pad horizontal-group pad))
