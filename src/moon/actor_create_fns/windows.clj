(ns moon.actor-create-fns.windows)

(defn create
  [{:keys [ctx/skin]
    :as ctx}
   window-create-fns]
  {:type :actor/group
   :actor/name "moon.ui.windows"
   :group/actors (for [f window-create-fns]
                   (f ctx))})
