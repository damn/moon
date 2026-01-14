(ns moon.ui.action-bar)

(defprotocol ActionBar
  (selected-skill [_])
  (add-skill! [_ {:keys [skill-id
                         texture-region
                         tooltip-text]} skin])
  (remove-skill! [_ skill-id]))
