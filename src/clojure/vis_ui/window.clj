(ns clojure.vis-ui.window
  (:import (com.kotcrab.vis.ui.widget VisWindow)))

(defn create
  [{:keys [title
           close-button?
           center?
           close-on-escape?]}]
  (let [show-window-border? true
        window (VisWindow. ^String title (boolean show-window-border?))]
    (when close-button?    (.addCloseButton window))
    (when center?          (.centerWindow   window))
    (when close-on-escape? (.closeOnEscape  window))
    window))
