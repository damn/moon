(ns clojure.scene2d.ui.widget-group)

(defprotocol WidgetGroup
  (pack! [_])
  (set-opts! [_ opts]))
