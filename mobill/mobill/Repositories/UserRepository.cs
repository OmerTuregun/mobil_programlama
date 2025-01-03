using mobill.Models;

namespace mobill.Repositories
{
    public class UserRepository
    {
        private readonly List<User> _users = new List<User>
        {
            new User { Id = 1, Name = "Ahmet Torul", Email = "ahmetemre3441@gmail.com" },
            new User { Id = 2, Name = "Jaime Lannister", Email = "jaime.lannister@gmail.com" }
        };

        public IEnumerable<User> GetAll() => _users;

        public User GetById(int id) => _users.FirstOrDefault(u => u.Id == id);

        public void Add(User user)
        {
            user.Id = _users.Count > 0 ? _users.Max(u => u.Id) + 1 : 1;
            _users.Add(user);
        }

        public void Update(User user)
        {
            var existingUser = GetById(user.Id);
            if (existingUser != null)
            {
                existingUser.Name = user.Name;
                existingUser.Email = user.Email;
            }
        }

        public void Delete(int id)
        {
            var user = GetById(id);
            if (user != null)
            {
                _users.Remove(user);
            }
        }
    }
}
