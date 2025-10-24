(ns clojure.vis-ui.menu-bar
  (:import (com.kotcrab.vis.ui.widget MenuBar)))

(defn create []
  (MenuBar.))

(defn table [menu-bar]
  (MenuBar/.getTable menu-bar))

(defn add-menu! [menu-bar menu]
  (MenuBar/.addMenu menu-bar menu))
