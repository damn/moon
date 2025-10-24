(ns clojure.vis-ui.tooltip
  (:import (com.kotcrab.vis.ui.widget Tooltip)))

(defn set-default-appear-delay-time! [amount]
  (set! Tooltip/DEFAULT_APPEAR_DELAY_TIME (float amount)))

(defn target [tooltip]
  (Tooltip/.getTarget tooltip))

(defn set-text! [tooltip text]
  (Tooltip/.setText tooltip (str text)))

(defn remove! [actor]
  (Tooltip/removeTooltip actor))

(defn create [{:keys [update-fn target content]}]
  (doto (proxy [Tooltip] []
          ; hooking into getWidth because at
          ; when tooltip position gets calculated we setText (which calls pack) before that
          ; so that the size is correct for the newly calculated text.
          (getWidth []
            (update-fn this)
            (let [^Tooltip this this]
              (proxy-super getWidth))))
    (.setTarget  target)
    (.setContent content)))
