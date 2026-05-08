(ns moon.ui.widget-group)

(defprotocol WidgetGroup
  (pack! [_])
  (set-opts! [_ opts]))
