(ns moon.scene2d.window
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Cell Skin Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout)))

(defn add-close-button! [window skin]
  (.add ^Table (.getTitleTable ^Window window)
        ^Actor (doto (TextButton. "X" ^Skin skin)
                 (.addListener (proxy [ChangeListener] []
                                 (changed [_event _actor]
                                   (.remove ^Actor window)))))))

(def ^:private set-opt-fns
  {:window/add-close-button? (fn [window skin _]
                               (add-close-button! window skin))})

(defn create [{:keys [title skin table/cell-defaults table/rows] :as opts}]
  (let [window (Window. ^String title ^Skin skin)]
    (when cell-defaults
      (.pad (.defaults window) (float (:pad cell-defaults))))
    (when rows
      (doseq [row rows]
        (doseq [cell row]
          (let [c (.add ^Table window ^Actor (:actor cell))]
            (when-let [w (:width cell)] (.width ^Cell c (float w)))
            (when-let [h (:height cell)] (.height ^Cell c (float h)))
            (when (:expand? cell) (.expand ^Cell c))
            (when-let [p (:pad cell)] (.pad ^Cell c (float p)))))
        (.row ^Table window))
      (.pack ^Layout window))
    (doseq [[k v] opts :when (and (set-opt-fns k) v)]
      ((set-opt-fns k) window skin v))
    window))
