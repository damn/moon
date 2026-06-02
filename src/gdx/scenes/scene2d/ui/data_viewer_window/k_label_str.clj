(ns gdx.scenes.scene2d.ui.data-viewer-window.k-label-str)

(defn k->label-str [k]
  (str "[LIGHT_GRAY]:"
       (when-let [ns (namespace k)] (str ns "/"))
       "[][WHITE]"
       (name k)
       "[]"))
