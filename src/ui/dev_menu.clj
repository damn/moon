(ns ui.dev-menu
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor Event Group Stage Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Image Label Skin Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn- set-label-text-actor [label-widget text-fn]
  (proxy [Actor] []
    (act [delta]
      (when (.getStage ^Actor this)
        (.setText ^Label label-widget ^String (text-fn)))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha])))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (Table.)]
     (doseq [cell [{:actor (Image. ^Texture icon)}
                   {:actor label}]]
       (.add ^Table sub-table ^Actor (:actor cell)))
     (.row ^Table sub-table)
     (.pack sub-table)
     (.addActor ^Group table (set-label-text-actor label text-fn))
     (doto (.add ^Table table ^Actor sub-table)
       (.right)
       (.expandX))))
  ([skin table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (.addActor ^Group table (set-label-text-actor label text-fn))
     (doto (.add ^Table table ^Actor label)
       (.right)
       (.expandX)))))

(defn- main-table [^Skin skin menus update-labels]
  (let [table (Table.)]
    (doseq [cell (for [{:keys [label items]} menus]
                   {:actor
                    (doto (TextButton. ^String label skin)
                      (.addListener (proxy [ChangeListener] []
                                      (changed [event actor]
                                        (let [^Stage stage (.getStage ^Event event)]
                                          (.addActor stage
                                                     (let [window (Window. ^String label skin)]
                                                       (doseq [{:keys [label on-click]} items]
                                                         (.add window (doto (TextButton. ^String label skin)
                                                                        (.addListener (proxy [ChangeListener] []
                                                                                        (changed [_event _actor]
                                                                                          (on-click)))))))
                                                       (.row window)
                                                       (.pack window)
                                                       (.add (.getTitleTable window)
                                                             (doto (TextButton. "X" skin)
                                                               (.addListener (proxy [ChangeListener] []
                                                                               (changed [_event _actor]
                                                                                 (.remove window))))))
                                                       window)))))))})]
      (.add table ^Actor (:actor cell)))
    (.row table)
    (.pack table)
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  [{:keys [menus update-labels skin]}]
  (let [table (Table.)]
    (doto (.add table ^Actor (main-table skin menus update-labels))
      (.expandX)
      (.fillX)
      (.colspan (int 1)))
    (.row table)
    (doto (.add table ^Actor (doto (Label. "" ^Skin skin)
                               (.setTouchable Touchable/disabled)))
      (.expand)
      (.fillX)
      (.fillY))
    (.row table)
    (doto table
      (.pack)
      (.setFillParent true))))
