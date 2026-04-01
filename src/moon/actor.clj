(ns moon.actor)

(defprotocol Actor
  (x [_])
  (y [_])
  (width [_])
  (height [_])
  (stage->local-coordinates [_ vector2])
  (add-listener! [_ listener])
  (user-object [_])
  (stage [_])
  (set-name! [_ name])
  (set-user-object! [_ object])
  (set-position! [_ [x y]]
                 [_ x y align])
  (set-visible! [_ visible?])
  (set-touchable! [_ touchable])
  (visible? [_])
  (hit [_ [x y] touchable?])
  (remove! [_])
  (parent [_]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn find-ancestor
  [actor clazz]
  (if-let [parent (parent actor)]
    (if (instance? clazz parent)
      parent
      (find-ancestor parent clazz))
    (throw (Error. (str "Actor has no parent window " actor)))))
